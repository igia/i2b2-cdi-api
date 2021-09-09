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


package io.igia.i2b2.cdi.derivedconcept.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.igia.i2b2.cdi.common.util.Graph;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptTopologicalSortDto;

@Component
public class DerivedConceptTopologicalSortWrapper {
    public DerivedConceptTopologicalSortDto detectDerivedConceptCyclicDependency(
        List<DerivedConceptDependencyDto> dependencies) {
    Map<String, Integer> pathMap = new HashMap<>();
    int[] incrementer = { 0 };

    for (int i = 0; i < dependencies.size(); i++) {
        final String derivedConceptPath = dependencies.get(i).getDerivedConceptPath();
        pathMap.computeIfAbsent(derivedConceptPath, conceptPath -> pathMap.put(conceptPath, incrementer[0]++));

        dependencies.get(i).setDerivedConceptPathIndex(pathMap.get(derivedConceptPath));

        final String parentConceptPath = dependencies.get(i).getParentConceptPath();
        pathMap.computeIfAbsent(parentConceptPath, conceptPath -> incrementer[0]++);

        dependencies.get(i).setParentConceptPathIndex(pathMap.get(parentConceptPath));
    }

    // Add edges and do topological sort
    Graph graph = new Graph(pathMap.size());
    for (DerivedConceptDependencyDto dependency : dependencies) {
        graph.addEdge(dependency.getParentConceptPathIndex(), dependency.getDerivedConceptPathIndex());
    }
    graph.topologicalSort();
    DerivedConceptTopologicalSortDto derivedConceptTopologicalSortDto = new DerivedConceptTopologicalSortDto();
    derivedConceptTopologicalSortDto.setOrder(graph.getOrder());
    derivedConceptTopologicalSortDto.setMessage(graph.getMessage());
    derivedConceptTopologicalSortDto.setPathMap(pathMap);
    return derivedConceptTopologicalSortDto;
    }

    /**
     * Get derived concepts topological sequence.
     * @param derivedConcepts - List of derived concepts for sorting
     * @param dependencies - List of dependencies for cycle detection.
     * @return
     */
    public List<DerivedConceptDto> getDerivedConceptTopologicalSequence(List<DerivedConceptDto> derivedConcepts,
            List<DerivedConceptDependencyDto> dependencies) {
        DerivedConceptTopologicalSortDto topologicalSortDto = detectDerivedConceptCyclicDependency(dependencies);
        // Reverse the Key values in Map
        Map<Integer, String> reversedPathMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : topologicalSortDto.getPathMap().entrySet()) {
            reversedPathMap.put(entry.getValue(), entry.getKey());
        }

        // Sort the dependency hierarchy
        List<DerivedConceptDto> sortedDerivedConcepts = new ArrayList<>();
        for (int i : topologicalSortDto.getOrder()) {
            for (DerivedConceptDto derivedConcept : derivedConcepts) {
                if (derivedConcept.getPath().equals(reversedPathMap.get(i))) {
                    sortedDerivedConcepts.add(derivedConcept);
                }
            }
        }
        return sortedDerivedConcepts;
    }
}
