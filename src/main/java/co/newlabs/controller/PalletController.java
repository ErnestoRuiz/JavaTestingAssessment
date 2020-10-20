package co.newlabs.controller;

import co.newlabs.dto.ItemDTO;
import co.newlabs.service.PalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/pallet")
public class PalletController {
    private PalletService service;

    @GetMapping("/{id}")
    public ResponseEntity getPalletById(@PathVariable int id) {
        return ResponseEntity.ok(service.getPalletById(id));
    }

    @PostMapping("/{id}/add")
    public ResponseEntity addItemToPallet(@PathVariable int id, @RequestBody ItemDTO item) {
        return ResponseEntity.ok(service.addItemToPallet(item, id));
    }

    @GetMapping("/{palletId}/remove/{itemId}")
    public ResponseEntity removeItemFromPallet(@PathVariable int palletId, @PathVariable int itemId) {
        return ResponseEntity.ok(service.removeItemFromPallet(itemId, palletId));
    }
}
