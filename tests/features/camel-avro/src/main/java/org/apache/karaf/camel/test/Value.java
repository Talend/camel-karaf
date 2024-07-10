/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.apache.karaf.camel.test;

@SuppressWarnings("all")
public class Value extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse(
            "{\"type\":\"record\",\"name\":\"Value\",\"namespace\":\"org.apache.camel.dataformat.avro.example\",\"fields\":[{\"name\":\"value\",\"type\":\"string\"}]}");
    @Deprecated
    public CharSequence value;

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return value;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, Object value$) {
        switch (field$) {
            case 0:
                value = (CharSequence) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'value' field.
     */
    public CharSequence getValue() {
        return value;
    }

    /**
     * Sets the value of the 'value' field.
     *
     * @param value the value to set.
     */
    public void setValue(CharSequence value) {
        this.value = value;
    }

    /** Creates a new Value RecordBuilder */
    public static Builder newBuilder() {
        return new Builder();
    }

    /** Creates a new Value RecordBuilder by copying an existing Builder */
    public static Builder newBuilder(
            Builder other) {
        return new Builder(other);
    }

    /** Creates a new Value RecordBuilder by copying an existing Value instance */
    public static Builder newBuilder(
            Value other) {
        return new Builder(other);
    }

    /**
     * RecordBuilder for Value instances.
     */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Value>
            implements org.apache.avro.data.RecordBuilder<Value> {

        private CharSequence value;

        /** Creates a new Builder */
        private Builder() {
            super(Value.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(Builder other) {
            super(other);
        }

        /** Creates a Builder by copying an existing Value instance */
        private Builder(Value other) {
            super(Value.SCHEMA$);
            if (isValidValue(fields()[0], other.value)) {
                this.value = data().deepCopy(fields()[0].schema(), other.value);
                fieldSetFlags()[0] = true;
            }
        }

        /** Gets the value of the 'value' field */
        public CharSequence getValue() {
            return value;
        }

        /** Sets the value of the 'value' field */
        public Builder setValue(CharSequence value) {
            validate(fields()[0], value);
            this.value = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'value' field has been set */
        public boolean hasValue() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'value' field */
        public Builder clearValue() {
            value = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        @Override
        public Value build() {
            try {
                Value record = new Value();
                record.value = fieldSetFlags()[0] ? this.value : (CharSequence) defaultValue(fields()[0]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}