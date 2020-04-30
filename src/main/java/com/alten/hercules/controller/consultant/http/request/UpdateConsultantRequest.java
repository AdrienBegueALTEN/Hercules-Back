package com.alten.hercules.controller.consultant.http.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alten.hercules.model.consultant.EConsultantFieldName;

/**
 * 
 * Represent the content of a consultant update request.
 *
 * @author Adrien Bègue
 *
 */
public class UpdateConsultantRequest {

    /**
     * Consultant identifier in database.
     * 
     * @see UpdateConsultantRequest#getId()
     * @see UpdateConsultantRequest#setId(Long)
     * 
     */
	@NotNull(message="'id' must be provided")
	private Long id;

    /**
     * Name of the database field to be changed.
     * 
     * @see UpdateConsultantRequest#getFieldName()
     * @see UpdateConsultantRequest#setFieldName(String)
     * @see EConsultantFieldName
     * 
     */
	@NotBlank(message="'fieldname' must be provided")
	private String fieldName;

    /**
     * New value of the database field.
     * 
     * @see UpdateConsultantRequest#getValue()
     * @see UpdateConsultantRequest#setValue(Object)
     * 
     */
	private Object value;

    /**
     * Empty constructor.
     * 
     */
	public UpdateConsultantRequest() {}
	
    /**
     * Constructor.
     * 
     * @see UpdateConsultantRequest#id
     * @see UpdateConsultantRequest#fieldName
     * @see UpdateConsultantRequest#value
     * 
     */
	public UpdateConsultantRequest(Long id, String fieldName, Object value) {
		this.id = id;
		this.fieldName = fieldName;
		this.value = value;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getFieldName() { return fieldName; }
	public void setFieldName(String fieldName) { this.fieldName = fieldName; }

	public Object getValue() { return value; }
	public void setValue(Object value) { this.value = value; }
	
}
