package com.zxy.aicodepro.model.enums

import lombok.Getter

@Getter
public enum class UserRoleEnum(val text: String, val value: String) {


    USER("用户", "user"),
    ADMIN("管理员", "admin");

    companion object {
        fun getUserRoleEnum(value: String): UserRoleEnum? {
            for (item in entries) {
                if (item.value == value) {
                    return item
                }
            }
            return null
        }
    }

}