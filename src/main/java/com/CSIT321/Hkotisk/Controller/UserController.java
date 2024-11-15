package com.CSIT321.Hkotisk.Controller;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import com.CSIT321.Hkotisk.Constant.ResponseCode;

import com.CSIT321.Hkotisk.Entity.ProductEntity;

import com.CSIT321.Hkotisk.Exception.ProductCustomException;

import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Response.ProductResponse;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RestController
@RequestMapping("/user")
public class UserController extends TextWebSocketHandler {

    private static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository prodRepo;


    private WebSocketSession webSocketSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.webSocketSession = session;
    }


    // Lists All Products
    @GetMapping("/product")
    public ResponseEntity<ProductResponse> getProducts(Authentication auth) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
            resp.setOblist(prodRepo.findAll());
        } catch (Exception e) {
            throw new ProductCustomException("Unable to retrieve products, please try again");
        }
        return new ResponseEntity<ProductResponse>(resp, HttpStatus.OK);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductEntity> getProductsById(@PathVariable int id) {
        ProductEntity products = prodRepo.findByProductId(id);
        return ResponseEntity.ok(products);
    }


}