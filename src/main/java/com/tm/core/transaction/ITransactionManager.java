package com.tm.core.transaction;

public interface ITransactionManager {
    <E> void useTransaction(E values);
}
