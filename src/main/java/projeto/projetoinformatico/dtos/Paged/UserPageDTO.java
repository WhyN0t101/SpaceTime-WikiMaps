package projeto.projetoinformatico.dtos.Paged;

import lombok.Data;
import projeto.projetoinformatico.dtos.UserDTO;

import java.util.List;

@Data
public class UserPageDTO {
    private List<UserDTO> users;
    private int currentPage;
    private int totalItems;
    private int totalPages;

    // Getters and setters

    public UserPageDTO(List<UserDTO> users, int currentPage, int totalItems, int totalPages) {
        this.users = users;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }
}
