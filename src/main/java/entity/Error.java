package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    private String filePath;
    private Integer line;
    private Integer beginLine;
    private Integer endLine;
    private String context;
    private String msg;


}

