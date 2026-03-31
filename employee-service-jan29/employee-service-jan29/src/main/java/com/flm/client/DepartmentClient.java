package com.flm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.flm.dto.DepartmentForm;
import com.flm.exception.DepartmentNotFoundException;

@FeignClient(name = "DEPARTMENT-SERVICE")
public interface DepartmentClient {
	
	@GetMapping("/dept/getDepartment/{id}")
	public DepartmentForm getDepartment(@PathVariable Long id) throws DepartmentNotFoundException;
}
