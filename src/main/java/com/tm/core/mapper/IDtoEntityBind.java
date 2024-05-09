package com.tm.core.mapper;

import java.util.Optional;

public interface IDtoEntityBind {
    Optional<String> get(String field);
    void setKey(String key);

}
