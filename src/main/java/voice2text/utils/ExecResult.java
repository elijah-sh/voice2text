package voice2text.utils;

import lombok.Data;

import java.util.List;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/12/10 21:03
 * @Description:
 */
@Data
public class ExecResult {

            private String type;
            private String message;
           // private File files;
            private List<Summary> summary;

}

@Data
class Summary{
    private String date;
    private String amount;
    private String number;
}

