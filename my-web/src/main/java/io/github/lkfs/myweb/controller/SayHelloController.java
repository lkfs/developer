package io.github.lkfs.myweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "my-web")
public class SayHelloController {
    @Operation(summary = "sayHello")
    @GetMapping("sayHello")
    public String sayHello(String name) {
        return "Hello " + name;
    }

}
