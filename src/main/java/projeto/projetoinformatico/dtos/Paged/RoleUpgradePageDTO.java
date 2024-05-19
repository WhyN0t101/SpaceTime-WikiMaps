package projeto.projetoinformatico.dtos.Paged;

import lombok.Data;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;

import java.util.List;

@Data
public class RoleUpgradePageDTO{
        private List<RoleUpgradeDTO> requests;
        private int currentPage;
        private int totalItems;
        private int totalPages;

        public RoleUpgradePageDTO(List<RoleUpgradeDTO> requests, int currentPage, int totalItems, int totalPages) {
            this.requests = requests;
            this.currentPage = currentPage;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }
}
