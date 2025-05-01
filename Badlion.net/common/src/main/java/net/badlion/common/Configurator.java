package net.badlion.common;

import java.util.*;

public class Configurator {

    private Map<String, Option> options = new LinkedHashMap<>();

    public Configurator() {

    }

    /**
     * By default all new options are required
     */
    public void addNewOption(String key, String niceName) {
        this.addNewOption(key, niceName, true);
    }

    /**
     * By default all new options are null
     */
    public void addNewOption(String key, String niceName, boolean required) {
        this.addNewOption(key, niceName, null, required);
    }

    /**
     * By default all new options are required
     */
    public void addNewOption(String key, String niceName, Object value) {
        this.addNewOption(key, niceName, value, true);
    }

    public void addNewOption(String key, String niceName, Object value, boolean required) {
        if (this.options.containsKey(key.toLowerCase())) {
            throw new RuntimeException("Key already exists");
        }

        Option option = new Option(key.toLowerCase(), niceName, value, required);
        this.options.put(key.toLowerCase(), option);
    }

    public void addNewOption(String key, String niceName, Object value, boolean required, Object[] validValues) {
        if (this.options.containsKey(key.toLowerCase())) {
            throw new RuntimeException("Key already exists");
        }

        Set<Object> validSet = new HashSet<>();
        Collections.addAll(validSet, validValues);

        Option option = new Option(key.toLowerCase(), niceName, value, required, validSet);
        this.options.put(key.toLowerCase(), option);
    }

    public void addNewBooleanOption(String key, String niceName, Object value, boolean required) {
        if (this.options.containsKey(key.toLowerCase())) {
            throw new NoSuchKeyExistsException("Key already exists");
        }

        Option option = new BooleanOption(key.toLowerCase(), niceName, value, required);
        this.options.put(key.toLowerCase(), option);
    }

    public void addNewIntegerOption(String key, String niceName, Object value, boolean required, int min, int max) {
        if (this.options.containsKey(key.toLowerCase())) {
            throw new NoSuchKeyExistsException("Key already exists");
        }

        // Old School logic fixes tho
        int tmp;
        if (min > max) {
            tmp = min;
            max = min;
            min = tmp;
        }

        Option option = new IntegerOption(key.toLowerCase(), niceName, value, required, min, max);
        this.options.put(key.toLowerCase(), option);
    }

	public Object updateOption(String key, String value) {
		Object obj = value;

		if (value.equalsIgnoreCase("t") || value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("f") || value.equalsIgnoreCase("false")) {
			obj = Boolean.valueOf(value);
		} else {
			try {
				obj = Integer.valueOf(value);
			} catch (NumberFormatException e) {

			}
		}

		return this.updateOption(key, obj);
	}

	public Object updateOption(String key, Object value) {
		if (!this.options.containsKey(key.toLowerCase())) {
			throw new NoSuchKeyExistsException("No key exists.");
		}

		return this.options.get(key.toLowerCase()).setValue(value);
	}

    public List<String> getOptionValues() {
        List<String> optionValues = new ArrayList<String>();

        for (Map.Entry<String, Option> entry : this.options.entrySet()) {
            optionValues.add(entry.getValue().toString());
        }

        return Collections.unmodifiableList(optionValues);
    }

    public Option getOption(String key) {
        return this.options.get(key.toLowerCase());
    }

    public IntegerOption getIntegerOption(String key) {
        return (IntegerOption) this.options.get(key.toLowerCase());
    }

    public BooleanOption getBooleanOption(String key) {
        return (BooleanOption) this.options.get(key.toLowerCase());
    }

    public List<Option> unconfiguredOptions() {
        List<Option> unconfiguredOptions = new ArrayList<>();

        for (Map.Entry<String, Option> entry : this.options.entrySet()) {
            if (entry.getValue().isRequired() && entry.getValue().getValue() == null) {
                unconfiguredOptions.add(entry.getValue());
            }
        }

        return unconfiguredOptions;
    }

    public boolean checkIfAllOptionsSet() {
        for (Map.Entry<String, Option> entry : this.options.entrySet()) {
            if (entry.getValue().isRequired() && entry.getValue().getValue() == null) {
                return false;
            }
        }

        return true;
    }

    public static class Option {

        protected String key;
        protected String niceName;
        protected Object value;
        protected boolean required;
        protected Set<Object> validValues;

        public Option(String key, String niceName, Object value, boolean required) {
            this.key = key;
            this.niceName = niceName;
            this.value = value;
            this.required = required;
            this.validValues = null;
        }

        public Option(String key, String niceName, Object value, boolean required, Set<Object> validValues) {
            this.key = key;
            this.niceName = niceName;
            this.value = value;
            this.required = required;
            this.validValues = validValues;
        }

        public String getKey() {
            return key;
        }

        public String getNiceName() {
            return niceName;
        }

        public Object getValue() {
            return value;
        }

        public boolean isRequired() {
            return required;
        }

        /**
         * Return null if we had a good value (weird right?)
         */
        public String setIfValidOption(Object value) {
            if (this.validValues == null || this.validValues.contains(value)) {
                this.value = value;
                return null;
            }

            return "Invalid Option Provided";
        }

        public Object setValue(Object value) {
            return this.setIfValidOption(value);
        }

        @Override
        public String toString() {
            if (this.value == null) {
                return this.niceName + " - Not Set";
            }

            return this.niceName + " - " + this.value;
        }
    }

    public static class BooleanOption extends Option {

        public BooleanOption(String key, String niceName, Object value, boolean required) {
            super(key, niceName, value, required);
        }

        @Override
        public String setIfValidOption(Object value) {
	        if (!(value instanceof Boolean)) {
		        return "Can only specify \"true\" or \"false\" for value";
	        }

	        // No need to cast here, we cast in the getter
	        this.value = value;

            return null;
        }

        @Override
        public Boolean getValue() {
            return (Boolean) this.value;
        }

    }

    public static class IntegerOption extends Option {

        private int min;
        private int max;

        public IntegerOption(String key, String niceName, Object value, boolean required, int min, int max) {
            super(key, niceName, value, required);
            this.min = min;
            this.max = max;
        }

        @Override
        public String setIfValidOption(Object value) {
	        if (!(value instanceof Integer)) {
		        return "Value must be number between " + this.min + " and " + this.max;
	        }

	        Integer num = ((Integer) value);

	        if (num >= this.min && num <= this.max) {
                this.value = value;
                return null;
            }

            return "Value must be number between " + this.min + " and " + this.max;
        }

        @Override
        public Integer getValue() {
            return (Integer) this.value;
        }

    }

	public static class NoSuchKeyExistsException extends RuntimeException {

		public NoSuchKeyExistsException(String msg) {
			super(msg);
		}

	}

	public static class ConfiguratorNotConfiguredException extends RuntimeException {

		public ConfiguratorNotConfiguredException(String msg) {
			super(msg);
		}

	}

	public static class ConfigurationConflictException extends RuntimeException {

		public ConfigurationConflictException(String msg) {
			super(msg);
		}

	}

}
