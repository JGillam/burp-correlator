/*
 * Copyright (c) 2015 Jason Gillam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.professionallyevil.bc;

import burp.IParameter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParametersTableModel extends AbstractTableModel {

    boolean showDecodedValues = true;
    List<CorrelatedParam> entries = new ArrayList<>();
    String[] columns = {"Name", "Type", "Requests", "Unique URLs", "Unique Values" , "Format", "Reflect %", "Decoded?","Example Value"};
    Class[] columnClasses = {String.class, String.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, Boolean.class, String.class};
    Map<CorrelatedParam, ParamInstance> samples = new HashMap<>();

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public void addParameters(Map<String, CorrelatedParam> parametersToAdd) {
        entries.addAll(parametersToAdd.values());
        fireTableDataChanged();
    }

    public CorrelatedParam getParameter(int row) {
        return entries.get(row);
    }

    public void clear() {
        entries.clear();
        samples.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CorrelatedParam param = entries.get(rowIndex);
        if (!samples.keySet().contains(param)) {
            ParamInstance sample = param.getSample();
            samples.put(param, sample);
        }

        ParamInstance sample = samples.get(param);

        switch (columnIndex){
            case 0:
                return sample.getName();
            case 1:
                int type = sample.getType();
                switch(type){
                    case IParameter.PARAM_URL:
                        return "URL";
                    case IParameter.PARAM_BODY:
                        return "Body";
                    case IParameter.PARAM_COOKIE:
                        return "Cookie";
                    default:
                        return "Other";
                }
            case 2:
                return param.getParamInstances(true).size();
            case 3:
                return param.getUniqueURLs().size();
            case 4:
                return param.getParamInstances(false).size();
            case 5:
                return param.getFormatString();
            case 6:
                int count = param.getReflectedCount();
                return count == 0?0:(100 * count / param.getParamInstances(true).size());
            case 7:
                return param.getDecodedReflectedCount() > 0;
            case 8:
                if(showDecodedValues) {
                    return sample.getDecodedValue();
                }  else {
                    return sample.getValue();
                }
            default:
                return "";
        }
    }

    public void setShowDecodedValues(boolean decoded) {
        showDecodedValues = decoded;
    }


}
