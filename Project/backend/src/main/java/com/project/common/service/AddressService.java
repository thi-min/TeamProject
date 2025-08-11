package com.project.common.service;

import com.project.common.dto.JusoSearchResponse;

public interface AddressService {
	JusoSearchResponse search(String keyword, int page, int size);
}
