package vintage.forgebackup.configuration;

import net.minecraftforge.common.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Section {
	String section();
	String comment();

	String GENERAL = Configuration.CATEGORY_GENERAL;
	String BLOCK = Configuration.CATEGORY_BLOCK;
	String ITEM = Configuration.CATEGORY_ITEM;
}
