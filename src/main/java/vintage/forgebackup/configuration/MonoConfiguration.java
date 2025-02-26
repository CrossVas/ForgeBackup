package vintage.forgebackup.configuration;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MonoConfiguration {
    private Configuration config;
    @SuppressWarnings("WeakerAccess")
    protected final Logger logger;

    public MonoConfiguration(Logger logger) {
        this.logger = logger;
    }

    protected void init(File configFile) {
        boolean forceSave = !configFile.exists();
        config = new Configuration(configFile);
        reload(forceSave);
    }

    public void reload() {
        reload(false);
    }

    @SuppressWarnings("WeakerAccess")
    public void reload(boolean forceSave) {
        try {
            config.load();
            Field[] fields = this.getClass().getDeclaredFields();

            migrateOldOptions(config);

            processSections(fields);
            processOptions(fields);

            if (forceSave || config.hasChanged()) {
                config.save();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "There was a problem loading the configuration." + e.getMessage());
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected void migrateOldOptions(Configuration config) {
    }

    private void processSections(Field[] fields)
            throws IllegalAccessException {
        for (Field field : fields) {
            Section section = field.getAnnotation(Section.class);
            if (section == null) {
                continue;
            }

            String comment = section.comment().isEmpty() ? null : section.comment();

            config.addCustomCategoryComment(section.section(), comment);
            if (field.getType() == ConfigCategory.class) {
                field.setAccessible(true);
                field.set(this, config.getCategory(section.section()));
            }
        }
    }

    private void processOptions(Field[] fields)
            throws IllegalAccessException {
        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);
            if (option == null) {
                continue;
            }

            String name = option.name();
            String comment = option.comment().isEmpty() ? null : option.comment();
            if (name.isEmpty()) {
                name = field.getName();
            }

            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            if (fieldType == boolean.class) {
                boolean value = field.getBoolean(this);
                value = config.get(option.section(), name, value, comment).getBoolean(value);
                field.set(this, value);
            } else if (fieldType == boolean[].class) {
                boolean[] value = (boolean[]) field.get(this);
                value = config.get(option.section(), name, value, comment).getBooleanList();
                field.set(this, value);
            } else if (fieldType == int.class) {
                int value = field.getInt(this);
                value = config.get(option.section(), name, value, comment).getInt(value);
                field.set(this, value);
            } else if (fieldType == int[].class) {
                int[] value = (int[]) field.get(this);
                value = config.get(option.section(), name, value, comment).getIntList();
                field.set(this, value);
            } else if (fieldType == double.class) {
                double value = field.getDouble(this);
                value = config.get(option.section(), name, value, comment).getDouble(value);
                field.set(this, value);
            } else if (fieldType == double[].class) {
                double[] value = (double[]) field.get(this);
                value = config.get(option.section(), name, value, comment).getDoubleList();
                field.set(this, value);
            } else if (fieldType == String.class) {
                String value = (String) field.get(this);
                value = config.get(option.section(), name, value, comment).getString();
                field.set(this, value);
            } else if (fieldType == String[].class) {
                String[] value = (String[]) field.get(this);
                value = config.get(option.section(), name, value, comment).getStringList();
                field.set(this, value);
            } else if (fieldType.isEnum()) {
                String value = ((Enum) field.get(this)).name();
                value = config.get(option.section(), name, value, comment).getString();
                //noinspection unchecked
                field.set(this, Enum.valueOf((Class<? extends Enum>) fieldType, value));
            } else {
                logger.warning(String.format("Skipping @Option \"%s\" with unknown type: %s", field.getName(), fieldType.getCanonicalName()));
            }
        }
    }
}
