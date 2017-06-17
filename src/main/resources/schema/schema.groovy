mgmt = graph.openManagement()

if(mgmt.getVertexLabel('Person') != null) {
    return;
}

personVertexLabel = mgmt.makeVertexLabel('Person').make()
motherEdgeLabel = mgmt.makeEdgeLabel('isMotherOf').make()
fatherEdgeLabel = mgmt.makeEdgeLabel('isFatherOf').make()
propertyKeyBirthDate = mgmt.makePropertyKey('birthDate').dataType(Long.class).make()
propertyKeyFirstName = mgmt.makePropertyKey('firstName').dataType(String.class).make()
propertyKeyLastName = mgmt.makePropertyKey('lastName').dataType(String.class).make()
propertyKeyGender = mgmt.makePropertyKey('gender').dataType(String.class).make()

mgmt.buildIndex('personByFirstName', Vertex.class).addKey(propertyKeyFirstName).buildCompositeIndex()
mgmt.buildIndex('mixedPersonByFirstName', Vertex.class).addKey(propertyKeyFirstName).buildMixedIndex("search")
mgmt.commit()

mgmt = graph.openManagement()
mgmt.awaitGraphIndexStatus(graph, 'personByFirstName').status(SchemaStatus.ENABLED).call()
mgmt.awaitGraphIndexStatus(graph, 'mixedPersonByFirstName').status(SchemaStatus.ENABLED).call()
mgmt.commit()