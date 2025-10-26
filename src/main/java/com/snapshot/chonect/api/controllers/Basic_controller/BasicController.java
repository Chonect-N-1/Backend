package com.snapshot.chonect.api.controllers.basic_controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public interface BasicController<T> extends
                GetByIdController<T> {
}
