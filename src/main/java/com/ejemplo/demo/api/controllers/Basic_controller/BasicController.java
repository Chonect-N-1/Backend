package com.ejemplo.demo.api.controllers.Basic_controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public interface BasicController<RESPONSE, RESPONSEBASIC, CREATEREQUEST, UPDATEREQUEST> extends
                DeleteController,
                GetByIdController<RESPONSE>, PostController<RESPONSEBASIC, CREATEREQUEST>,
                PutController<RESPONSE, UPDATEREQUEST> {
}