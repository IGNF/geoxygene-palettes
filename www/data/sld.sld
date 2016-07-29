<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
	<NamedLayer>
		<Name>cite:Population_commune_1999_2010_GIP_region</Name>
		<UserStyle>
			<FeatureTypeStyle>
				<Rule>
					<Filter xmlns:ogc="http://www.opengis.net/ogc">
						    <And>
						      <PropertyIsGreaterThan>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>24</Literal>
						      </PropertyIsGreaterThan>
						      <PropertyIsLessThanOrEqualTo>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>145</Literal>
						      </PropertyIsLessThanOrEqualTo>
						    </And>
					  </Filter>
					  <PolygonSymbolizer>
					    <Fill>
					      <CssParameter name="fill">#C1E3DC</CssParameter>
					    </Fill>
					    <Stroke>
					      <CssParameter name="stroke">#000000</CssParameter>
					      <CssParameter name="stroke-width">.1</CssParameter>
					      <CssParameter name="stroke-linejoin">bevel</CssParameter>
					    </Stroke>
					  </PolygonSymbolizer>
				</Rule>
				<Rule>
					<Filter xmlns:ogc="http://www.opengis.net/ogc">
						    <And>
						      <PropertyIsGreaterThan>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>13</Literal>
						      </PropertyIsGreaterThan>
						      <PropertyIsLessThanOrEqualTo>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>24</Literal>
						      </PropertyIsLessThanOrEqualTo>
						    </And>
					  </Filter>
					  <PolygonSymbolizer>
					    <Fill>
					      <CssParameter name="fill">#8FADA8</CssParameter>
					    </Fill>
					    <Stroke>
					      <CssParameter name="stroke">#000000</CssParameter>
					      <CssParameter name="stroke-width">.1</CssParameter>
					      <CssParameter name="stroke-linejoin">bevel</CssParameter>
					    </Stroke>
					  </PolygonSymbolizer>
				</Rule>

				<Rule>
					<Filter xmlns:ogc="http://www.opengis.net/ogc">
						    <And>
						      <PropertyIsGreaterThan>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>4</Literal>
						      </PropertyIsGreaterThan>
						      <PropertyIsLessThanOrEqualTo>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>13</Literal>
						      </PropertyIsLessThanOrEqualTo>
						    </And>
					  </Filter>
					  <PolygonSymbolizer>
					    <Fill>
					      <CssParameter name="fill">#5E7B76</CssParameter>
					    </Fill>
					    <Stroke>
					      <CssParameter name="stroke">#000000</CssParameter>
					      <CssParameter name="stroke-width">.1</CssParameter>
					      <CssParameter name="stroke-linejoin">bevel</CssParameter>
					    </Stroke>
					  </PolygonSymbolizer>
				</Rule>

				<Rule>
					<Filter xmlns:ogc="http://www.opengis.net/ogc">
						    <And>
						      <PropertyIsGreaterThan>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>-25</Literal>
						      </PropertyIsGreaterThan>
						      <PropertyIsLessThanOrEqualTo>
							<PropertyName>p1999_2010</PropertyName>
							<Literal>4</Literal>
						      </PropertyIsLessThanOrEqualTo>
						    </And>
					  </Filter>
					  <PolygonSymbolizer>
					    <Fill>
					      <CssParameter name="fill">#07211E</CssParameter>
					    </Fill>
					    <Stroke>
					      <CssParameter name="stroke">#000000</CssParameter>
					      <CssParameter name="stroke-width">.1</CssParameter>
					      <CssParameter name="stroke-linejoin">bevel</CssParameter>
					    </Stroke>
					  </PolygonSymbolizer>
				</Rule>
			</FeatureTypeStyle>
		</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>
