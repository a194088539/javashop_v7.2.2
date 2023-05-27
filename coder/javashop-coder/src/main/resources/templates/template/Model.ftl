package ${packageStr};

import java.io.Serializable;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
${importStr}

/**
 * ${model.model_name}实体
 * @author ${username}
 * @version v${version}
 * @since v${since}
 * ${time}
 */
@Table(name="${model.table_name}")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ${className} implements Serializable {
			
    private static final long serialVersionUID = ${serialVersionNum};
    
${propertiesStr}
${methodStr}
${equalsMethodStr}
${hashMethodStr}
${toStringMethodStr}
	
}