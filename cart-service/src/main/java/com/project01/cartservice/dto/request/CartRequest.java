package com.project01.cartservice.dto.request;


public record CartRequest(String userId,Long productId,double totalAmount,int quantityOrder){
}
