package com.ddisearch.service;

import org.springframework.stereotype.Service;

@Service
public interface BasicService {
    String handleSearch(String drugA, String drugB);

}
