package gov.iti.jets.domain.models;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonbPropertyOrder( {"id", "name", "description", "price", "categories"} )
public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int id;
    private String name;
    private String description;
    private int quantity;
    private BigDecimal price;
    @ManyToMany( fetch = FetchType.EAGER )
    private Set<Category> categories;

    public Product() {
        this.categories = new HashSet<>();
    }

    public Product( String name, String description, int quantity, BigDecimal price ) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.categories = new HashSet<>();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity( int quantity ) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice( BigDecimal price ) {
        this.price = price;
    }

    @XmlTransient
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories( Set<Category> categories ) {
        this.categories = categories;
    }

    public void addCategoryToProduct( Category category ) {
        this.categories.add( category );
        category.getProducts().add( this );
    }

    public void removeCategoryFromProduct( Category category ) {
        this.categories.remove( category );
        category.getProducts().remove( this );
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", categories=" + categories.size() +
                '}';
    }
}