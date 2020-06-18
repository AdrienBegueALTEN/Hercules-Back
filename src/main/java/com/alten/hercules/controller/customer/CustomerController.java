package com.alten.hercules.controller.customer;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.alten.hercules.model.mission.ESheetStatus;
import com.alten.hercules.model.user.AppUser;
import com.alten.hercules.model.user.Manager;
import com.alten.hercules.service.StoreImage;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {

	@Autowired
	private CustomerDAL dal;

	@Autowired
	private StoreImage storeImage;

	@ApiOperation(value = "List of all customers", notes = "Return a list of all customer present is the database. "
			+ "Two format can be chosen : basic or normal. Normal formal returns all field and basic format returns only the id, "
			+ "the name and the activity sector.")
	@GetMapping
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

	@ApiOperation(value = "Detail of a customer", notes = "Return the details of a customer provided by the id as parameters.")
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable Long id) {

		Optional<Customer> customer = dal.findById(id);

		if (id == null || !customer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().body(customer);

	}
	
	@ApiOperation(value = "List of all missions of a customer", notes = "Provide the missions linked to a customer (given by the id as parameter).")
	@GetMapping("/{id}/missions")
	public ResponseEntity<?> getAllByCustomer(@PathVariable Long id){
		AppUser user = ((AppUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		Optional<Long> optManagerId = Optional.ofNullable(user instanceof Manager ? user.getId() : null);
		if(optManagerId.isPresent())
			return ResponseEntity.ok(this.dal.findMissionsByCustomer(id).stream()
				.map(mission -> new CompleteMissionResponse(mission, false, true))
				.collect(Collectors.toList()));
		else
			return ResponseEntity.ok(this.dal.findMissionsByCustomer(id).stream()
					.filter(mission -> mission.getSheetStatus().equals(ESheetStatus.VALIDATED))
					.map(mission -> new CompleteMissionResponse(mission, false, false))
					.collect(Collectors.toList()));
	}

	@ApiOperation(value = "Create a new customer", notes = "Create a new customer in the database. It needs a request with the name, the activity sector "
			+ "and the description (not mandatory).")
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

	@ApiOperation(value = "Update a field of a customer", notes = "ddd")
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

	@ApiOperation(value = "Delete a customer", notes = "Delete a customer from the database. It checks if the given id is a customer id, then if the "
			+ "consultant is not linked to some missions, it delete the customer, otherwise it won't do the deletion.")
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

	@ApiOperation(value = "Link a logo to a customer", notes = "Upload the image file in the server and link the file name to the customer given as parameter "
			+ "if the id is a customer id. ")
	@PostMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		try {
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
				if(extension.equals("jpg") ||
				   extension.equals("png") ||
				   extension.equals("gif")) {
				Customer customer = dal.findById(id).orElseThrow(() -> new ResourceNotFoundException(Customer.class));
				if(customer.getLogo()!=null) {
					this.storeImage.delete(StoreImage.LOGO_FOLDER+customer.getLogo());
					customer.setLogo(null);
				}
				storeImage.save(file,"logo");
				customer.setLogo(file.getOriginalFilename());
				this.dal.save(customer);
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}
	
	@ApiOperation(value = "Dowload a logo file", notes = "Provide the logo as a file with the file name as parameter.")
	@GetMapping("/logo/{fileName:.+}")
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
	
	@ApiOperation(value = "Delete a logo from a consultant", notes = "If the customer is found, it set to null the value of the logo and "
			+ "delete the file from the server.")
	@DeleteMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> deleteLogo(@PathVariable Long id){
		try {
			Customer customer = this.dal.findById(id).orElseThrow(() -> new ResourceNotFoundException(Customer.class));
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
