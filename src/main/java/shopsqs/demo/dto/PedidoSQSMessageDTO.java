package shopsqs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoSQSMessageDTO {
    private String pedidoId;
    private String usuarioNombre;
    private String usuarioEmail;
    private List<ItemPedidoDTO> items;
    private double precioTotal;

}

