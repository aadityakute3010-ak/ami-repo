package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyName;   // e.g. temperature, humidity

    private String category; // Firmware, Hardware, Network, Protocol
    
    private String unit;      // optional: °C, %, V

    @Builder.Default 
    private boolean active = true; 
}