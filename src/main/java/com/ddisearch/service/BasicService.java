package com.ddisearch.service;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import org.springframework.stereotype.Service;

@Service
public interface BasicService {

    String handleSearch(String drugA, String drugB);
}
