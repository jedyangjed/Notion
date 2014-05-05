package edu.mayo.qia.pacs.rest;

import io.dropwizard.hibernate.UnitOfWork;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.resource.PerRequest;

import edu.mayo.qia.pacs.components.Connector;
import edu.mayo.qia.pacs.components.Device;
import edu.mayo.qia.pacs.components.Pool;
import edu.mayo.qia.pacs.components.Query;

@Scope("prototype")
@Component
@PerRequest
public class QueryEndpoint {
  static Logger logger = Logger.getLogger(QueryEndpoint.class);

  @Autowired
  SessionFactory sessionFactory;

  @Autowired
  JdbcTemplate template;

  public int poolKey;

  @PUT
  @Path("/{id: [1-9][0-9]*}")
  @UnitOfWork
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response update(@PathParam("id") int id, Query update) {
    Session session = sessionFactory.getCurrentSession();
    Query query;
    query = (Query) session.byId(Query.class).load(id);
    if (query == null || query.pool.poolKey != poolKey) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the query")).build();
    }
    query.update(update);
    return getQuery(id);
  }

  @PUT
  @Path("/{id: [1-9][0-9]*}/fetch")
  @UnitOfWork
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response fetch(@PathParam("id") int id) {
    Session session = sessionFactory.getCurrentSession();
    Query query;
    query = (Query) session.byId(Query.class).load(id);
    if (query == null || query.pool.poolKey != poolKey) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the query")).build();
    }
    query.doFetch();
    return getQuery(id);
  }

  @POST
  @UnitOfWork
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createQuery(@FormDataParam("file") InputStream spreadSheetInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("connectorKey") int connectorKey) throws Exception {

    ;
    logger.info("handling  " + fileDetail.getFileName());
    // Use POI or SuperCSV to parse
    fileDetail.getFileName();
    if (fileDetail.getFileName().toLowerCase().endsWith(".xls")) {
      // Parse and create a query
    }
    Session session = sessionFactory.getCurrentSession();
    Query query;
    Connector connector = (Connector) session.byId(Connector.class).load(connectorKey);

    int destinationPoolKey = connector.destinationPoolKey;
    int deviceKey = connector.queryDeviceKey;
    int queryPoolKey = connector.queryPoolKey;

    Pool pool = (Pool) session.byId(Pool.class).load(poolKey);
    if (pool == null) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the pool")).build();
    }

    Pool destinationPool = (Pool) session.byId(Pool.class).load(destinationPoolKey);
    if (destinationPool == null) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the destination")).build();
    }

    Device device = (Device) session.byId(Device.class).load(deviceKey);

    if (device == null || device.pool.poolKey != queryPoolKey) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the device")).build();
    }
    query = Query.constructQuery(fileDetail.getFileName(), spreadSheetInputStream);
    query.pool = pool;
    query.device = device;
    query.destinationPool = destinationPool;
    session.save(query);
    session.getTransaction().commit();
    query.executeQuery();
    return Response.ok(query).build();
  }

  @GET
  @Path("/{id: [1-9][0-9]*}")
  @UnitOfWork
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getQuery(@PathParam("id") int id) {
    Session session = sessionFactory.getCurrentSession();
    Query query;
    query = (Query) session.byId(Query.class).load(id);
    if (query == null || query.pool.poolKey != poolKey) {
      return Response.status(Status.NOT_FOUND).entity(new SimpleResponse("message", "Could not load the query")).build();
    }
    return Response.ok(query).build();
  }
}