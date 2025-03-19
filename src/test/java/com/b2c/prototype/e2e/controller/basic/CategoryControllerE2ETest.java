package com.b2c.prototype.e2e.controller.basic;

import com.b2c.prototype.e2e.BasicE2ETest;
import com.b2c.prototype.e2e.util.TestUtil;
import com.b2c.prototype.modal.dto.payload.constant.CategoryDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerE2ETest extends BasicE2ETest {

    private static final String URL_TEMPLATE = "/api/v1/category";

    @BeforeEach
    public void cleanUpDatabase() {
        cleanUpDb(8);
    }

    private void cleanUpDb(int count) {
        try (Connection connection = connectionHolder.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM category");

            statement.execute("ALTER SEQUENCE category_id_seq RESTART WITH " + count);
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean table: category", e);
        }
    }

    @Test
    void testPostCategory() {
        cleanUpDb(3);
        loadDataSet("/datasets/item/category/emptyE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/CategoryDto.json")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/testE2ECategoryDataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testPutSingleCategory() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(put(URL_TEMPLATE)
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/UpdateSingleCategoryDto.json")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/updateE2ESingleCategoryDataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testPutCategory1() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(put(URL_TEMPLATE + "/inner")
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/UpdateCategory1Dto.json")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/updateE2ECategory1DataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testPutCategory2() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(put(URL_TEMPLATE + "/inner")
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/UpdateCategory2Dto.json")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/updateE2ECategory2DataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testPutCategory3() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(put(URL_TEMPLATE + "/inner")
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/UpdateCategory3Dto.json")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/updateE2ECategory3DataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testPutCategoryException() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(put(URL_TEMPLATE + "/inner")
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.readFile("json/category/input/IncorrectCategoryDto.json")))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/testE2ECategoryDataSet.yml",
                new String[] {"id"},
                new String[] {"label", "value"}
        );
    }

    @Test
    void testDeleteCategory() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");

        try {
            mockMvc.perform(delete(URL_TEMPLATE)
                            .params(getMultiValueMap(getRequestParams())))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verifyExpectedData("/datasets/item/category/deleteE2ECategoryDataSet.yml");
    }

    @Test
    void testGetCategory() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(get(URL_TEMPLATE)
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            CategoryDto actual = objectMapper.readValue(jsonResponse, CategoryDto.class);
            String expectedResultStr = TestUtil.readFile("json/category/output/ResponseCategoryDto.json");
            CategoryDto expected = objectMapper.readValue(expectedResultStr, CategoryDto.class);
            assertEquals(expected, actual);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error processing the JSON response", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllFirstLineCategories() {
        loadDataSet("/datasets/item/category/testE2ECategoryDataSet.yml");
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(get(URL_TEMPLATE + "/all")
                            .params(getMultiValueMap(getRequestParams()))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            List<CategoryDto> actual = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            String expectedResultStr = TestUtil.readFile("json/category/output/ResponseAllCategoryDto.json");
            List<CategoryDto> expected = objectMapper.readValue(expectedResultStr, new TypeReference<>() {});
            assertEquals(expected, actual);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error processing the JSON response", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getRequestParams() {
        return Map.of("category", "Gaming Laptops & Accessories");
    }
}
