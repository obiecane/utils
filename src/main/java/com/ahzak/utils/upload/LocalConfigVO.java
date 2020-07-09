package com.ahzak.utils.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class LocalConfigVO {

    private String validFileFullPath;

    private Config.LocalConfig localConfig;
}
