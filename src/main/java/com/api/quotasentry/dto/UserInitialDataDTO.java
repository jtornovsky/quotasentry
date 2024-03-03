package com.api.quotasentry.dto;

import com.api.quotasentry.model.DbType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInitialDataDTO extends UserDTO {
    private DbType targetDb;
}
