package com.alten.hercules.controller.customer;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alten.hercules.controller.customer.http.request.AddCustomerRequest;
import com.alten.hercules.controller.customer.http.response.BasicCustomerResponse;
import com.alten.hercules.controller.mission.http.response.CompleteMissionResponse;
import com.alten.hercules.dal.CustomerDAL;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.service.StoreImage;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {

	@Autowired
	private CustomerDAL dal;

	@Autowired
	private StoreImage storeImage;

	@GetMapping("")
	public ResponseEntity<Object> getAllCustomer(@RequestParam(required = false) Boolean basic) {
		if (basic == null || !basic)
			return ResponseEntity.ok(dal.findAll());
		List<BasicCustomerResponse> customers = new ArrayList<>();
		dal.findAll().forEach((customer) -> {
			customers.add(new BasicCustomerResponse(customer));
		});
		;

		return ResponseEntity.ok(customers);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable Long id) {

		Optional<Customer> customer = dal.findById(id);

		if (id == null || !customer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().body(customer);

	}
	
	@GetMapping("/{id}/missions")
	public ResponseEntity<?> getAllByCustomer(@PathVariable Long id){
		return ResponseEntity.ok(this.dal.findMissionsByCustomer(id).stream()
				.map(mission -> new CompleteMissionResponse(mission, false, true))
				.collect(Collectors.toList()));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> addCustomer(@Valid @RequestBody AddCustomerRequest request) {
		Optional<Customer> optCustomer = dal.findByNameIgnoreCase(request.getName());
		if (optCustomer.isPresent())
			return ResponseEntity.accepted().body(optCustomer.get().getId());

		Customer customer = request.buildCustomer();
		dal.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer.getId());
	}

	@PutMapping
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> updateCustomer(@Valid @RequestBody Customer customer) {

		if (this.dal.findById(customer.getId()) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (customer.getName() == null || customer.getName().isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		dal.save(customer);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		Optional<Customer> optCustomer = dal.findById(id);
		if (!optCustomer.isPresent())
			return ResponseEntity.notFound().build();

		Customer customer = optCustomer.get();
		if (!customer.getMissions().isEmpty())
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		this.storeImage.delete(StoreImage.LOGO_FOLDER+customer.getLogo());
		dal.delete(customer);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		try {
			Customer customer = dal.findById(id).orElseThrow(() -> new ResourceNotFoundException("customer"));
			if(customer.getLogo()!=null) {
				this.storeImage.delete(StoreImage.LOGO_FOLDER+customer.getLogo());
				customer.setLogo(null);
			}
			storeImage.save(file,"logo");
			customer.setLogo(file.getOriginalFilename());
			this.dal.save(customer);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}
	
	@GetMapping("/logo/{fileName:.+}")
	@PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storeImage.loadFileAsResource(fileName,"logo");
        if(resource == null) return ResponseEntity.notFound().build();

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {System.err.println(ex);} 
        
        if(contentType == null) contentType = "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        
    }
	
	@DeleteMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	private ResponseEntity<?> deletePicture(@PathVariable Long id){
		try {
			Customer customer = this.dal.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer"));
			if(customer.getLogo()!=null) {
				this.storeImage.delete(StoreImage.LOGO_FOLDER+customer.getLogo());
				customer.setLogo(null);
			}
			this.dal.save(customer);
		} catch (ResourceNotFoundException e) {
			return e.buildResponse();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
