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
import org.springframework.web.bind.annotation.RequestPart;
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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Class that manages the requests sent to the API for the customers.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hercules/customers")
public class CustomerController {
	
	/**
	 * DAL for the customers.
	 */
	@Autowired
	private CustomerDAL dal;
	
	/**
	 * Object that manages the operation for the pictures.
	 */
	@Autowired
	private StoreImage storeImage;
	
	
	/**
	 * Function that gives a list with the information for all the customers.
	 * @param basic Boolean that indicates if the information of the customers will be basic or complete
	 * @return 200 with the basic or complete list<br>401 Authentication problem.
	 */
	@ApiOperation(value = "List of all customers", notes = "Return a list of all customer present is the database. "
			+ "Two format can be chosen : basic or normal. Normal formal returns all field and basic format returns only the id, "
			+ "the name and the activity sector.")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentificated token.")
	})
	@GetMapping
	public ResponseEntity<Object> getAllCustomer(@ApiParam("Boolean that indicates if the information of the customers will be basic or complete")
												 @RequestParam(required = false) Boolean basic) {
		if (basic == null || !basic)
			return ResponseEntity.ok(dal.findAll());
		List<BasicCustomerResponse> customers = new ArrayList<>();
		dal.findAll().forEach((customer) -> {
			customers.add(new BasicCustomerResponse(customer));
		});
		;

		return ResponseEntity.ok(customers);
	}
	
	/**
	 * Function that gives back the details of a specific customer if he exists.
	 * @param id ID of the customer
	 * @return 200 The details of the customer are sent<br>404 The user is not found<br>401 Authentication problem.
	 */
	@ApiOperation(value = "Detail of a customer", notes = "Return the details of a customer provided by the id as parameters.")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentificated token."),
		@ApiResponse(code = 404, message="Customer not found.")
	})
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@ApiParam("ID of the customer")@PathVariable Long id) {

		Optional<Customer> customer = dal.findById(id);

		if (id == null || !customer.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().body(customer);

	}
	
	/**
	 * Functions that gives back a list of all the missions of a specific customer if he exists.
	 * @param id ID of the customer
	 * @return 200 A list of the missions is given<br>401 Authentication problem.
	 */
	@ApiOperation(value = "List of all missions of a customer", notes = "Provide the missions linked to a customer (given by the id as parameter).")
	@ApiResponses({
		@ApiResponse(code = 200, message="OK."),
		@ApiResponse(code = 401, message="Invalid authentificated token.")
	})
	@GetMapping("/{id}/missions")
	public ResponseEntity<?> getAllByCustomer(@ApiParam("ID of the customer")@PathVariable Long id){
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
	
	/**
	 * Function that adds a new customer in the database with the information given in the request.
	 * @param request Request with the information : name and activity sector of the customer.
	 * @return 201 The customer is created<br>202 The customer already exists<br>401 Authentication problem.
	 */
	@ApiOperation(value = "Create a new customer", notes = "Create a new customer in the database. It needs a request with the name, the activity sector "
			+ "and the description (not mandatory).")
	@ApiResponses({
		@ApiResponse(code = 201, message="Customer is created."),
		@ApiResponse(code = 202, message="Customer already exists."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager.")
	})
	@PostMapping
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> addCustomer(@ApiParam(
			"name : customer's name;\n"
			+ "activitySector : customer's activity sector;\n"
		)@Valid @RequestBody AddCustomerRequest request) {
		Optional<Customer> optCustomer = dal.findByNameIgnoreCase(request.getName());
		if (optCustomer.isPresent())
			return ResponseEntity.accepted().body(optCustomer.get().getId());

		Customer customer = request.buildCustomer();
		dal.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer.getId());
	}
	
	/**
	 * Function that modifies the specific field of a specific customer, given information in the request.
	 * @param customer object Customer with the modified fields.
	 * @return 404 The consultant is not found<br>400 if the field name cannot be found or the value is of wrong type<br>200 The customer is updated.
	 */
	@ApiOperation(value = "Update a field of a customer", 
				  notes = "Update a field of the customer in the database by saving the customer received in the request.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Customer updated."),
		@ApiResponse(code = 401, message="Field name is not found or value is of wrong type."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Customer not found.")
	})
	@PutMapping
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> updateCustomer(@ApiParam("Object Customer with modified fields")@Valid @RequestBody Customer customer) {

		if (this.dal.findById(customer.getId()) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (customer.getName() == null || customer.getName().isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		dal.save(customer);
		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	/**
	 * Function that deletes a specific customer using the id in a DELETE request.
	 * @param id ID of the customer
	 * @return 200 The customer is deleted<br>401 Authentication problem<br>404 The customer is not found<br>409 The customer is not deleted because he is linked to some missions.
	 */
	@ApiOperation(value = "Delete a customer", 
			notes = "Delete a customer from the database. It checks if the given id is a customer id, then if the "
			+ "consultant is not linked to some missions, it delete the customer, otherwise it won't do the deletion.")
	@ApiResponses({
		@ApiResponse(code = 200, message="Customer is deleted."),
		@ApiResponse(code = 401, message="Invalid authentificated token or user isn't manager."),
		@ApiResponse(code = 404, message="Customer not found."),
		@ApiResponse(code = 409, message="Customer is linked to 1 or more missions.")
	})
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> deleteCustomer(@ApiParam("ID of the customer")@PathVariable Long id) {
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
	
	/**
	 * Function that receives a picture file from the request and then saves it and updates the customer.
	 * @param blob file with the picture of the logo
	 * @param id ID of the customer
	 * @return 200 The logo is saved and the customer is updated<br>400 The extension of the file is not supported<br>401 Authentication problem<br>404 The customer is not found.
	 */
	@ApiOperation(value = "Link a logo to a customer", 
			notes = "Upload the image file in the server and link the file name to the customer given as parameter "
			+ "if the id is a customer id. ")
	@ApiResponses({
		@ApiResponse(code = 401, message="Invalid authentification token."),
		@ApiResponse(code = 404, message="Customer is not found"),
		@ApiResponse(code = 200, message="Image is uploaded and customer line is modified with the new image name in database."),
		@ApiResponse(code = 400, message="The extension of the file is not good.")
	})
	@PostMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> uploadLogo(@ApiParam("Blob with the logo")@RequestPart("blob") MultipartFile blob, 
			@ApiParam("Name of logo")@RequestPart("name") String name,
			@ApiParam("ID of the customer")@PathVariable Long id) {
		try {
			String extension = FilenameUtils.getExtension(name).toLowerCase();
			if(extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg") 
					|| extension.equals("gif") || extension.equals("webp") || extension.equals("ico") 
					|| extension.equals("svg")) {
				Customer customer = dal.findById(id).orElseThrow(() -> new ResourceNotFoundException(Customer.class));
				if(customer.getLogo()!=null) {
					this.storeImage.delete(StoreImage.LOGO_FOLDER+customer.getLogo());
					customer.setLogo(null);
				}
				storeImage.save(blob, name, "logo");
				customer.setLogo(name);
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
	
	/**
	 * Function that gives back a picture file of a logo, given the name of the file.
	 * @param fileName Name of the file
	 * @param request Request
	 * @return 200 An image is given back<br>401 Authentication problem<br>404 The image is not found.
	 */
	@ApiOperation(value = "Dowload a logo file", notes = "Provide the logo as a file with the file name as parameter.")
	@ApiResponses({
		@ApiResponse(code = 401, message="Invalid token."),
		@ApiResponse(code = 404, message="No image is found with this name"),
		@ApiResponse(code = 200, message="An image is found and returned")
	})
	@GetMapping("/logo/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@ApiParam("Name of the file")@PathVariable String fileName, 
    		@ApiParam("Request")HttpServletRequest request) {
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
	
	/**
	 * Function that deletes the logo of a specific customer.
	 * @param id ID of the customer
	 * @return 200 The image is deleted and the customer updated<br>401 Authentication problem<br>404 The customer is not found.
	 */
	@ApiOperation(value = "Delete a logo from a customer", notes = "If the customer is found, it set to null the value of the logo and "
			+ "delete the file from the server.")
	@ApiResponses({
		@ApiResponse(code = 401, message="Invalid token."),
		@ApiResponse(code = 404, message="Customer is not found"),
		@ApiResponse(code = 200, message="Image is deleted and customer's line is modified with null value in database.")
	})
	@DeleteMapping("/{id}/logo")
	@PreAuthorize("hasAuthority('MANAGER')")
	public ResponseEntity<?> deleteLogo(@ApiParam("ID of the customer")@PathVariable Long id){
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
