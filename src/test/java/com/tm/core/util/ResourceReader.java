package com.tm.core.util;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import java.io.InputStream;

public final class ResourceReader {
    public IDataSet getDataSet(String fileName) throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        return new FlatXmlDataSetBuilder().build(inputStream);
    }
}
