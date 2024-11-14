
package com.CSIT321.Hkotisk.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.Entity.ProductEntity;
import com.CSIT321.Hkotisk.Exception.ProductCustomException;
import com.CSIT321.Hkotisk.Repository.ProductRepository;
import com.CSIT321.Hkotisk.Response.ProductResponse;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private ProductRepository prodRepo;

    @GetMapping("/products/{category}")
    public ResponseEntity<List<ProductEntity>> getProductsByCategory(@PathVariable String category) {
        List<ProductEntity> products = prodRepo.findByCategory(category);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable int id) {
        ProductEntity product = prodRepo.findByProductId(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products/quantity/{id}")
    public ResponseEntity<Integer> getProductQuantityById(@PathVariable int id) {
        ProductEntity product = prodRepo.findByProductId(id);
        if (product != null) {
            return ResponseEntity.ok(product.getQuantity());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/product")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductEntity input) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            ProductEntity prod = new ProductEntity();
            prod.setDescription(input.getDescription());
            prod.setPrices(input.getPrices());
            if (input.getPrices() != null) {
                prod.setPrices(Arrays.stream(input.getPrices()).toArray());
            }
            prod.setProductName(input.getProductName());
            prod.setQuantity(input.getQuantity());
            if (input.getSizes() != null) {
                prod.setSizes(Arrays.stream(input.getSizes()).map(String::toUpperCase).toArray(String[]::new));
            }
            prod.setCategory(input.getCategory());
            if (input.getProductImage() != null) {
                prod.setProductImage(input.getProductImage());
            }
            prodRepo.save(prod);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.ADD_SUCCESS_MESSAGE);
            resp.setOblist(prodRepo.findAll());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            throw new ProductCustomException("Unable to save product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }


    @PutMapping("/product")
    public ResponseEntity<ServerResponse> updateProducts(@Valid @RequestBody ProductEntity productDTO) throws IOException {
        ServerResponse resp = new ServerResponse();
        try {
            ProductEntity prod;
            if (productDTO.getProductImage() != null) {
                prod = new ProductEntity(productDTO.getProductId(), productDTO.getDescription(), productDTO.getProductName(),
                        productDTO.getPrices(), productDTO.getQuantity(), productDTO.getSizes(), productDTO.getCategory(), productDTO.getProductImage());
            } else {
                ProductEntity prodOrg = prodRepo.findByProductId(productDTO.getProductId());
                prod = new ProductEntity(productDTO.getProductId(), productDTO.getDescription(), productDTO.getProductName(),
                        productDTO.getPrices(), productDTO.getQuantity(), productDTO.getSizes(), productDTO.getCategory(), prodOrg.getProductImage());
            }
            prodRepo.save(prod);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.UPD_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new ProductCustomException("Unable to update product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @DeleteMapping("/product")
    public ResponseEntity<ProductResponse> delProduct(@RequestParam int productId) throws IOException {
        ProductResponse resp = new ProductResponse();
        try {
            prodRepo.deleteByProductId(productId);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.DEL_SUCCESS_MESSAGE);
        } catch (Exception e) {
            throw new ProductCustomException("Unable to delete product details, please try again");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }


}
