package diboot.core.test.binder.entity;

import com.diboot.core.data.copy.Accept;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
public class UserImportModel implements Serializable {

    @Accept(name = "genderLabel.label")
    private String label;

    @Accept(name = "birthdate")
    private LocalDate date;

}
