package hello;

import com.googlecode.charts4j.*;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@SpringUI @Theme("valo") public class VaadinUI extends UI
{

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	private final Grid grid;

	private final TextField filter;

	private final Button addNewBtn;

	private final Button refreshBtn;
	final LineChart chart;
	final Plot plot;

	@Autowired public VaadinUI(CustomerRepository repo, CustomerEditor editor)
	{
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid();
		this.filter = new TextField();
		plot = Plots.newPlot(Data.newData(0, 66.6, 33.3, 100));
		chart = GCharts.newLineChart(plot);
		chart.setTitle("My Really Great Chart");
		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
		refreshBtn = new Button("refresh", FontAwesome.REFRESH);
	}

	@Override protected void init(VaadinRequest request)
	{
		// build layout
		HorizontalLayout actions = new HorizontalLayout(refreshBtn, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions, filter, grid, editor);
		setContent(mainLayout);

		// Configure layouts and components
		actions.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setInputPrompt("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.addTextChangeListener(e -> listCustomers(e.getText()));

		// Connect selected Customer to editor or hide if none is selected
		grid.addSelectionListener(e -> {
			if (e.getSelected().isEmpty())
			{
				editor.setVisible(false);
			}
			else
			{
				editor.editCustomer((Customer) e.getSelected().iterator().next());
			}
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

		refreshBtn.addClickListener(e -> {
			listCustomers("");
			editor.setVisible(false);
			filter.clear();
		});

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});

		// Initialize listing
		listCustomers(null);
	}

	// tag::listCustomers[]
	private void listCustomers(String text)
	{
		if (StringUtils.isEmpty(text))
		{
			grid.setContainerDataSource(new BeanItemContainer<Customer>(Customer.class, repo.findAll()));
		}
		else
		{
			grid.setContainerDataSource(new BeanItemContainer<Customer>(Customer.class, repo.findByLastNameStartsWithIgnoreCase(text)));
		}
	}
	// end::listCustomers[]

}