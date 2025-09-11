package com.chonect.backend.api.dto.response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
@SuperBuilder
@Data
public class UserResponse extends UserBasicResponse {
}