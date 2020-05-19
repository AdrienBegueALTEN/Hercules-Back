package com.alten.hercules.controller.customer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.alten.hercules.dao.customer.CustomerDAO;
import com.alten.hercules.model.customer.Customer;
import com.alten.hercules.model.exception.ResourceNotFoundException;
import com.alten.hercules.service.StoreImage;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {

	@Autowired
	private CustomerDAO dao;

	@Autowired
	private StoreImage storeImage;

	@GetMapping("")
	public ResponseEntity<Object> getAllCustomer(@RequestParam(required = false) Boolean basic) {
		if (basic == null || !basic)
			return ResponseEntity.ok(dao.findAll());
		List<BasicCustomerResponse> customers = new ArrayList<>();
		dao.findAll().forEach((customer) -> {
			customers.add(new BasicCustomerResponse(customer));
		});
		;

		return ResponseEntity.ok(customers);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable Long id) {

		Optional<Customer> customer = dao.findById(id);

		if (id == null || !customer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().body(customer);

	}

	@PostMapping
	public ResponseEntity<?> addCustomer(@Valid @RequestBody AddCustomerRequest request) {
		Optional<Customer> optCustomer = dao.findByNameIgnoreCase(request.getName());
		if (optCustomer.isPresent())
			return ResponseEntity.accepted().body(optCustomer.get().getId());

		Customer customer = request.buildCustomer();
		dao.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer.getId());
	}

	@PutMapping
	public ResponseEntity<?> updateCustomer(@Valid @RequestBody Customer customer) {

		if (this.dao.findById(customer.getId()) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (customer.getName() == null || customer.getName().isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		dao.save(customer);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		Optional<Customer> optCustomer = dao.findById(id);
		if (!optCustomer.isPresent())
			return ResponseEntity.notFound().build();

		Customer customer = optCustomer.get();
		if (!customer.getMissions().isEmpty())
			return new ResponseEntity<>(HttpStatus.CONFLICT);

		dao.delete(customer);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/search")
	public List<Customer> searchCustomer(@RequestParam(name = "q") List<String> keys) {
		List<Customer> customers = new ArrayList<>();
		for (String key : keys) {
			customers.addAll(this.dao.findByNameOrActivitySector(key));
		}
		List<Customer> listRes = new ArrayList<>(new HashSet<>(customers));
		return listRes;
	}

	@PostMapping("/{id}/upload-logo")
	public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		try {
			Customer customer = dao.findById(id).orElseThrow(() -> new ResourceNotFoundException("customer"));
			if(customer.getLogo()!=null) {
				this.storeImage.delete("img/logo/"+customer.getLogo());
				customer.setLogo(null);
			}
			storeImage.save(file,"logo");
			customer.setLogo(file.getOriginalFilename());
			this.dao.save(customer);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}
	
	@GetMapping("/logo/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storeImage.loadFileAsResource(fileName,"logo");

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        	System.err.println(ex);
        } catch (NullPointerException npe) {
        	contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
	

}
