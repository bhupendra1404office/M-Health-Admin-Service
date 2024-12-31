package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginateDto {
    private Object content;
    private Integer size;
    private Integer noOfElements;
    private Integer totalPages;
    private Integer totalElements;
}
