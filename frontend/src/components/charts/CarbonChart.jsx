import React, { useEffect, useRef } from 'react';
import * as d3 from 'd3';

const CarbonChart = ({ data }) => {
  const svgRef = useRef();

  useEffect(() => {
    if (!data || data.length === 0) return;

    // Dimensions
    const width = 600;
    const height = 400;
    const margin = { top: 20, right: 30, bottom: 40, left: 50 };

    // Clear previous chart
    d3.select(svgRef.current).selectAll('*').remove();

    // Create SVG
    const svg = d3
      .select(svgRef.current)
      .attr('width', width)
      .attr('height', height);

    // Scales
    const x = d3
      .scaleTime()
      .domain(d3.extent(data, (d) => new Date(d.date)))
      .range([margin.left, width - margin.right]);

    const y = d3
      .scaleLinear()
      .domain([0, d3.max(data, (d) => d.carbon)])
      .nice()
      .range([height - margin.bottom, margin.top]);

    // Line generator
    const line = d3
      .line()
      .x((d) => x(new Date(d.date)))
      .y((d) => y(d.carbon))
      .curve(d3.curveMonotoneX);

    // Add gradient
    const gradient = svg
      .append('defs')
      .append('linearGradient')
      .attr('id', 'carbon-gradient')
      .attr('x1', '0%')
      .attr('y1', '0%')
      .attr('x2', '0%')
      .attr('y2', '100%');

    gradient.append('stop').attr('offset', '0%').attr('stop-color', '#10b981').attr('stop-opacity', 0.8);
    gradient.append('stop').attr('offset', '100%').attr('stop-color', '#10b981').attr('stop-opacity', 0.1);

    // Add area
    const area = d3
      .area()
      .x((d) => x(new Date(d.date)))
      .y0(height - margin.bottom)
      .y1((d) => y(d.carbon))
      .curve(d3.curveMonotoneX);

    svg
      .append('path')
      .datum(data)
      .attr('fill', 'url(#carbon-gradient)')
      .attr('d', area);

    // Add line
    svg
      .append('path')
      .datum(data)
      .attr('fill', 'none')
      .attr('stroke', '#10b981')
      .attr('stroke-width', 3)
      .attr('d', line);

    // Add dots
    svg
      .selectAll('.dot')
      .data(data)
      .enter()
      .append('circle')
      .attr('class', 'dot')
      .attr('cx', (d) => x(new Date(d.date)))
      .attr('cy', (d) => y(d.carbon))
      .attr('r', 5)
      .attr('fill', '#059669')
      .attr('stroke', 'white')
      .attr('stroke-width', 2)
      .on('mouseover', function (event, d) {
        d3.select(this).attr('r', 8);
        // Tooltip
        svg
          .append('text')
          .attr('class', 'tooltip')
          .attr('x', x(new Date(d.date)))
          .attr('y', y(d.carbon) - 15)
          .attr('text-anchor', 'middle')
          .attr('fill', '#064e3b')
          .attr('font-weight', 'bold')
          .text(`${d.carbon} kg CO₂`);
      })
      .on('mouseout', function () {
        d3.select(this).attr('r', 5);
        svg.selectAll('.tooltip').remove();
      });

    // X Axis
    svg
      .append('g')
      .attr('transform', `translate(0,${height - margin.bottom})`)
      .call(d3.axisBottom(x).ticks(5))
      .attr('color', '#6b7280');

    // Y Axis
    svg
      .append('g')
      .attr('transform', `translate(${margin.left},0)`)
      .call(d3.axisLeft(y).ticks(5))
      .attr('color', '#6b7280');

    // Y Axis Label
    svg
      .append('text')
      .attr('transform', 'rotate(-90)')
      .attr('y', 15)
      .attr('x', -(height / 2))
      .attr('text-anchor', 'middle')
      .attr('fill', '#374151')
      .text('CO₂ Économisé (kg)');

  }, [data]);

  return (
    <div className="bg-white p-6 rounded-xl shadow-lg">
      <h3 className="text-xl font-bold mb-4">Impact Carbone par Session</h3>
      <svg ref={svgRef}></svg>
    </div>
  );
};

export default CarbonChart;