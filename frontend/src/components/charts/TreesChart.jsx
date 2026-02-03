import React, { useEffect, useRef } from 'react';
import * as d3 from 'd3';

const TreesChart = ({ data }) => {
  const svgRef = useRef();

  useEffect(() => {
    if (!data || data.length === 0) return;

    const width = 600;
    const height = 400;
    const margin = { top: 20, right: 30, bottom: 60, left: 50 };

    d3.select(svgRef.current).selectAll('*').remove();

    const svg = d3
      .select(svgRef.current)
      .attr('width', width)
      .attr('height', height);

    // Scales
    const x = d3
      .scaleBand()
      .domain(data.map((d) => d.month))
      .range([margin.left, width - margin.right])
      .padding(0.2);

    const y = d3
      .scaleLinear()
      .domain([0, d3.max(data, (d) => d.trees)])
      .nice()
      .range([height - margin.bottom, margin.top]);

    // Bars
    svg
      .selectAll('.bar')
      .data(data)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', (d) => x(d.month))
      .attr('y', (d) => y(d.trees))
      .attr('width', x.bandwidth())
      .attr('height', (d) => height - margin.bottom - y(d.trees))
      .attr('fill', '#34d399')
      .on('mouseover', function (event, d) {
        d3.select(this).attr('fill', '#10b981');
        svg
          .append('text')
          .attr('class', 'tooltip')
          .attr('x', x(d.month) + x.bandwidth() / 2)
          .attr('y', y(d.trees) - 10)
          .attr('text-anchor', 'middle')
          .attr('fill', '#064e3b')
          .attr('font-weight', 'bold')
          .text(`${d.trees} arbres`);
      })
      .on('mouseout', function () {
        d3.select(this).attr('fill', '#34d399');
        svg.selectAll('.tooltip').remove();
      });

    // X Axis
    svg
      .append('g')
      .attr('transform', `translate(0,${height - margin.bottom})`)
      .call(d3.axisBottom(x))
      .selectAll('text')
      .attr('transform', 'rotate(-45)')
      .style('text-anchor', 'end')
      .attr('fill', '#6b7280');

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
      .text('Nombre d\'Arbres Plantés');

  }, [data]);

  return (
    <div className="bg-white p-6 rounded-xl shadow-lg">
      <h3 className="text-xl font-bold mb-4">Arbres Plantés par Mois</h3>
      <svg ref={svgRef}></svg>
    </div>
  );
};

export default TreesChart;