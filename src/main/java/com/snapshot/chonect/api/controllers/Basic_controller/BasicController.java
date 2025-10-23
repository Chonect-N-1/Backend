package com.snapshot.chonect.api.controllers.Basic_controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public interface BasicController<RESPONSE, REQUEST, UPDATEREQUEST> extends
                GetByIdController<RESPONSE> {
}
