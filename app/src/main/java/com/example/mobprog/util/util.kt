package com.example.mobprog.util

fun titleLengthCheck(title: String): String{
    if(title.length > 25){
        return title.take(25).plus("...")
    }
    return title
}