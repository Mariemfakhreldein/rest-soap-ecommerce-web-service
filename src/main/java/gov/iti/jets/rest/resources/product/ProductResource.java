package gov.iti.jets.rest.resources.product;

import gov.iti.jets.domain.models.Product;
import gov.iti.jets.domain.services.ProductService;
import gov.iti.jets.rest.beans.PaginationData;
import gov.iti.jets.rest.resources.category.*;
import gov.iti.jets.rest.utils.ApiUtils;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path( "products" )
@Consumes( {"application/json; qs=1", "application/xml; qs=0.75"} )
@Produces( {"application/json; qs=1", "application/xml; qs=0.75"} )
public class ProductResource {
    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAllProducts( @BeanParam PaginationData paginationData,
                                    @BeanParam ProductFilters filters,
                                    @QueryParam( "fields" ) List<String> fieldsWanted ) {

        List<Product> products = ProductService.findProducts( paginationData, filters );

        List<ProductResponse> productResponses = products.stream()
                .map( ProductResponse::new )
                .collect( toList() );

        productResponses.forEach( this::addLinksToProductResponse );

        ApiUtils.nullifyListFieldsForPartialResponse( productResponses, fieldsWanted, ProductResponse.class );

        List<Link> links = ApiUtils.createPaginatedResourceLinks( uriInfo, paginationData, ProductService.getNumberOfCategories() );

        ProductResponseWrapper productResponseWrapper = new ProductResponseWrapper( productResponses, links );

        return Response.ok().entity( productResponseWrapper ).build();
    }

    private void addLinksToProductResponse( ProductResponse productResponse ) {
        productResponse.addLink( ApiUtils.createSelfLink( uriInfo, productResponse.getId(), ProductResource.class ) );
        productResponse.getCategories().forEach( cr -> CategoryResource.addLinksToCategoryResponse( cr, uriInfo ) );
    }

    @POST
    public Response createProduct( ProductRequest productRequest ) {
        ProductValidator.validate( productRequest );

        System.out.println(productRequest);

        Product product = new Product( productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getQuantity(),
                productRequest.getPrice() );

        ProductService.createProduct( product );

        ProductResponse productResponse = new ProductResponse( product );
        addLinksToProductResponse( productResponse );

        URI createdAtUri = ApiUtils.getCreatedAtUriForPostRequest( uriInfo, productResponse.getId() );

        return Response.created( createdAtUri ).entity( productResponse ).build();
    }

/*
    @GET
    @Path( "{id}" )
    public Response findCategoryById( @PathParam( "id" ) int id ) {
        Category category = CategoryService.findCategoryById( id ).orElseThrow( () -> new ApiException(
                String.format( "No category exists with the id (%s)", id ), 400 ) );
        CategoryResponse categoryResponse = new CategoryResponse( category );
        addLinksToCategoryResponseWithIdPathParam( categoryResponse );
        return Response.ok().entity( categoryResponse ).build();
    }

    private void addLinksToCategoryResponseWithIdPathParam( CategoryResponse categoryResponse ) {
        categoryResponse.addLink( ApiUtils.createSelfLink( uriInfo ) );
    }

    @DELETE
    @Path( "{id}" )
    public Response deleteCategory( @PathParam( "id" ) int id ) {
        CategoryService.deleteCategory( id );
        return Response.noContent().build();
    }

    @PUT
    @Path( "{id}" )
    public Response updateCategory( @PathParam( "id" ) int id, CategoryRequest categoryRequest ) {
        CategoryValidator.ensureCategoryRequestIsValid( categoryRequest );

        Category category = new Category( categoryRequest.getName() );
        category.setId( id );

        CategoryService.updateCategory( category );

        CategoryResponse categoryResponse = new CategoryResponse( category.getId(), category.getName() );

        addLinksToCategoryResponseWithIdPathParam( categoryResponse );

        return Response.ok().entity( categoryResponse ).build();
    }
     */
}