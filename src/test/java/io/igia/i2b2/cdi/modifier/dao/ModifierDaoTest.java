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

package io.igia.i2b2.cdi.modifier.dao;

import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan("io.igia.i2b2.cdi.modifier.dao")
@DirtiesContext
@Sql({"/test-schema.sql", "/test-modifier-data.sql"})
public class ModifierDaoTest {

    @Autowired
    private ModifierDao modifierDao;

    @Test
    public void findModifiers_noRecords() {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setSource("unknown");

        List<ModifierDto> modifiers = modifierDao.findModifiers(searchDto);
        assertThat(modifiers).isNotNull().isEmpty();
    }

    @Test
    public void findModifiers_noFilterCriteria() {
        List<ModifierDto> modifiers = modifierDao.findModifiers(new ModifierSearchDto());
        assertThat(modifiers).isNotNull().isNotEmpty().size().isEqualTo(3);

        ModifierDto expectedModifier = createModifier("1", "test1", "/test/1", "demo");
        assertThat(modifiers.get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    @Test
    public void findModifiers_filterBySourceSystem() {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setSource("test");
        List<ModifierDto> modifiers = modifierDao.findModifiers(searchDto);
        assertThat(modifiers).isNotNull().isNotEmpty().size().isEqualTo(1);

        ModifierDto expectedModifier = createModifier("2", "test2", "/test/2", "test");
        assertThat(modifiers.get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    @Test
    public void findModifiers_filterByModifierPaths() {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setModifierPaths(Arrays.asList("/test/2", "/test/3"));
        List<ModifierDto> modifiers = modifierDao.findModifiers(searchDto);
        assertThat(modifiers).isNotNull().isNotEmpty().size().isEqualTo(2);

        ModifierDto expectedModifier = createModifier("2", "test2", "/test/2", "test");
        assertThat(modifiers.get(0)).isEqualToComparingFieldByField(expectedModifier);
        ModifierDto expectedModifier1 = createModifier("3", "test3", "/test/3", "DEMO");
        assertThat(modifiers.get(1)).isEqualToComparingFieldByField(expectedModifier1);
    }

    @Test
    public void findModifiers_filterByModifierPaths_and_filterBySourceSystem() {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setSource("demo");
        searchDto.setModifierPaths(Arrays.asList("/test/2", "/test/3"));
        List<ModifierDto> modifiers = modifierDao.findModifiers(searchDto);
        assertThat(modifiers).isNotNull().isNotEmpty().size().isEqualTo(1);

        ModifierDto expectedModifier = createModifier("3", "test3", "/test/3", "DEMO");
        assertThat(modifiers.get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    @Test
    public void findModifiers_filterBySourceSystem_caseInsensitive() {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setSource("teST");
        List<ModifierDto> modifiers = modifierDao.findModifiers(searchDto);
        assertThat(modifiers).isNotNull().isNotEmpty().size().isEqualTo(1);

        ModifierDto expectedModifier = createModifier("2", "test2", "/test/2", "test");
        assertThat(modifiers.get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    private ModifierDto createModifier(String code, String name, String path, String source) {
        ModifierDto modifierDto = new ModifierDto();
        modifierDto.setCode(code);
        modifierDto.setName(name);
        modifierDto.setModifierPath(path);
        modifierDto.setSource(source);
        return modifierDto;
    }

}
