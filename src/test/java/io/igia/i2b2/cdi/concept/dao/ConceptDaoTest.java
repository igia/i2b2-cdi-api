/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/


package io.igia.i2b2.cdi.concept.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.common.dto.Operator;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.dto.PathFilterDto;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({ "io.igia.i2b2.cdi.concept.dao", "io.igia.i2b2.cdi.common.database", "io.igia.i2b2.cdi.common.dto"})
@Sql({ "/test-schema.sql", "/test-concept-data.sql" })
@DirtiesContext
public class ConceptDaoTest {

    @Autowired
    private ConceptDao conceptDao;

    @Test
    public void findConcepts_noRecords() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setSource("notpresent");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isEmpty();
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void findConcepts_noFilterCriteria() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto) ;
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(3);

        ConceptDto expectedConcept = createConcept("1", "test1", "/test/1/", "demo");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void findConcepts_filterBySourceSystem() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setSource("test");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void findConcepts_filterByConceptCode() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setCode("2");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_cd");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());

        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void findConcepts_filterByConceptCode_and_filterBySourceCode() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setCode("2");
        searchDto.setSource("test");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }
    
    @Test
    public void findConcepts_filterBySourceCode_and_filterByConceptPath() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setSource("test");
        PathFilterDto pathFilterDto = new PathFilterDto();
        pathFilterDto.setPath("test");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(pathFilterDto);

        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }
    
    @Test
    public void findConceptsFilterBySourceCodeAndConceptPaths() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        List<String> paths = new ArrayList<>();
        paths.add("/test/1/");
        paths.add("/test/2/");
        searchDto.setConceptPaths(paths);
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);

        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(1)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }
    
    @Test
    public void findConcepts_filterBySourceCode_and_filterByConceptPath_and_pagination() {
        ConceptSearchDto searchDto = createConceptSearchDto("test", "test");
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void findConcepts_filterBySourceSystem_caseInsensitive() {
        ConceptSearchDto searchDto = new ConceptSearchDto();
        searchDto.setSource("TeST");
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        searchDto.setPageableDto(pageableDto);
        searchDto.setPathFilterDto(new PathFilterDto());
        
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(searchDto);
        assertThat(concepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);

        ConceptDto expectedConcept = createConcept("2", "test2", "/test/2/", "test");
        assertThat(concepts.getRecords().get(0)).isEqualToComparingFieldByField(expectedConcept);
        assertThat(concepts.getTotalCount()).isEqualTo(0);
    }

    private ConceptDto createConcept(String code, String name, String path, String source) {
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode(code);
	conceptDto.setName(name);
	conceptDto.setConceptPath(path);
	conceptDto.setSource(source);
	return conceptDto;
    }

    @Test
    public void testAddConcept() {
	ConceptDto expectedConcept = createConcept("5", "test5", "/test/5/", "test");
	int actualResult = conceptDao.addConcept(expectedConcept);
	assertThat(actualResult).isEqualTo(1);
    }

    @Test
    public void testUpdateConcept() {
	ConceptDto expectedConcept = createConcept("1", "test1", "\\Derived\\Test12\\", "test");
	int actualResult = conceptDao.updateConcept(expectedConcept, "/test/1/");
	assertThat(actualResult).isEqualTo(1);
    }

    @Test
    public void testDeleteConcept() {
	ConceptDto expectedConcept = createConcept("1", "test1", "/test/1/", "demo");
	int actualResult = conceptDao.deleteConcept(expectedConcept);
	assertThat(actualResult).isEqualTo(1);
    }
    
    private ConceptSearchDto createConceptSearchDto(String source, String path) {
        ConceptSearchDto conceptSearchDto = new ConceptSearchDto();
        conceptSearchDto.setSource(source);
        
        PathFilterDto pathFilterDto = new PathFilterDto();
        pathFilterDto.setPath(path);
        pathFilterDto.setOpertaor(Operator.CONTAINS);
        conceptSearchDto.setPathFilterDto(pathFilterDto);
        
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("concept_path");
        conceptSearchDto.setPageableDto(pageableDto);
        return conceptSearchDto;
    }
}
