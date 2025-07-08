
package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInforRefundTicketDTO {
    private String userFullName;
    private String userEmail;
    private String userIDCardNumber;
    private String userPhoneNumber;

    private List<RefundTicketDTO> refundTicketDTOs; 
}