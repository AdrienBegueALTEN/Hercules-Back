package com.alten.hercules.controller.http.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alten.hercules.model.consultant.EConsultantFieldname;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * Represent the content of an entity update request.
 *
 * @author Adrien Bègue
 *
 */
public class UpdateEntityRequest {

    /**
     * Entity identifier in database.
     * 
     * @see UpdateEntityRequest#getId()
     * @see UpdateEntityRequest#setId(Long)
     * 
     */
	@ApiModelProperty("Entity identifier.")
	@NotNull
	private Long id;

    /**
     * Name of the database field to update.
     * 
     * @see UpdateEntityRequest#getFieldName()
     * @see UpdateEntityRequest#setFieldName(String)
     * @see EConsultantFieldname
     * 
     */
	@ApiModelProperty("Name of field to update.")
	@NotBlank
	private String fieldName;

    /**
     * New value of the database field.
     * 
     * @see UpdateEntityRequest#getValue()
     * @see UpdateEntityRequest#setValue(Object)
     * 
     */
	@ApiModelProperty("New value of field.")
	private Object value;

    /**
     * Empty constructor.
     * 
     */
	public UpdateEntityRequest() {}
	
    /**
     * Constructor.
     * 
     * @see UpdateEntityRequest#id
     * @see UpdateEntityRequest#fieldName
     * @see UpdateEntityRequest#value
     * 
     */
	public UpdateEntityRequest(Long id, String fieldName, Object value) {
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
