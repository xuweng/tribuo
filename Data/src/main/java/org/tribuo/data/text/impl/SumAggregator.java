/*
 * Copyright (c) 2015-2020, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tribuo.data.text.impl;

import com.oracle.labs.mlrg.olcut.provenance.ConfiguredObjectProvenance;
import com.oracle.labs.mlrg.olcut.provenance.impl.ConfiguredObjectProvenanceImpl;
import org.tribuo.Feature;
import org.tribuo.data.text.FeatureAggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A feature aggregator that aggregates occurrence counts across a number of
 * feature lists.
 */
public class SumAggregator implements FeatureAggregator {

    private final ThreadLocal<Map<String,Double>> map = ThreadLocal.withInitial(HashMap::new);

    @Override
    public List<Feature> aggregate(List<Feature> input) {
        Map<String,Double> curMap = map.get();
        curMap.clear();

        for (Feature f : input) {
            double curValue = f.getValue();
            curMap.merge(f.getName(),curValue,Double::sum);
        }

        List<Feature> features = new ArrayList<>();

        for (Map.Entry<String,Double> e : curMap.entrySet()) {
            features.add(new Feature(e.getKey(),e.getValue()));
        }

        return features;
    }

    @Override
    public ConfiguredObjectProvenance getProvenance() {
        return new ConfiguredObjectProvenanceImpl(this,"FeatureAggregator");
    }
}
