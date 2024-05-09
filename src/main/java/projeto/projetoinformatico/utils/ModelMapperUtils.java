package projeto.projetoinformatico.utils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

@Component
public class ModelMapperUtils {

    private static ModelMapper modelMapper;

    @Autowired
    public ModelMapperUtils(ModelMapper modelMapper) {
        ModelMapperUtils.modelMapper = modelMapper;
    }

    public static <D, T> D map(T source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public static <D, T> D map(T source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    public  UserDTO userToDTO(User user, Class<UserDTO> destinationType) {
        return modelMapper.map(user, destinationType);
    }
    public  LayerDTO layerToDTO(Layer layer, Class<LayerDTO> destinationType) {
        return modelMapper.map(layer, destinationType);
    }
    public  RoleUpgradeDTO roleUpgradeToDTO(RoleUpgrade upgrade, Class<RoleUpgradeDTO> destinationType) {
        return modelMapper.map(upgrade, destinationType);
    }
    public User dtoToUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }
    public Layer dtoToLayer(LayerDTO dto) {
        return modelMapper.map(dto, Layer.class);
    }
    public RoleUpgrade dtoToUpgrade(RoleUpgradeDTO dto) {
        return modelMapper.map(dto, RoleUpgrade.class);
    }
}