package com.project01.productservice.ship;


import com.project01.productservice.entity.Ship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl {

    private final ShipRepository shipRepository;

    public List<ShipDTO> getAllShip() {
        List<Ship> ship = shipRepository.findAll();
        List<ShipDTO> shipDTOS = new ArrayList<>();
        for (Ship ship1 : ship) {
            shipDTOS.add(new ShipDTO(ship1.getId(),ship1.getName()));
        }
        return shipDTOS;
    }

    public void createShip(ShipDTO shipDTO) {
        Ship ship = Ship.builder()
                .name(shipDTO.getName())
                .build();
        shipRepository.save(ship);
    }
}
