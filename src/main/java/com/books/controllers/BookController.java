package com.books.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {


    @GetMapping(path = "/book")
    public ResponseEntity<String> greeting(){
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}