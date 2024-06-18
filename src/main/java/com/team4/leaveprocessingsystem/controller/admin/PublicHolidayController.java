package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/manage-ph")
public class PublicHolidayController {

    private final PublicHolidayService publicHolidayService;

    @Autowired
    public PublicHolidayController(PublicHolidayService publicHolidayService) {
        this.publicHolidayService = publicHolidayService;
    }

    @GetMapping("/")
    public String viewAllPublicHolidays(Model model) {
        List<PublicHoliday> publicHolidays = publicHolidayService.findAll();
        model.addAttribute("publicHolidays", publicHolidays);

        // Get list of years for dropdown
        List<Integer> years = publicHolidayService.findAllYears();
        model.addAttribute("years", years);

        return "admin/manage-ph/view-all";
    }

    @GetMapping("/filter")
    public String filterPublicHolidays(@RequestParam(required = false) String searchType,
                                       @RequestParam(required = false) String query,
                                       @RequestParam(required = false) String year,
                                       Model model) {
        List<PublicHoliday> publicHolidays = publicHolidayService.findAll(searchType, query, year);
        model.addAttribute("publicHolidays", publicHolidays);

        // Get list of years for dropdown
        List<Integer> years = publicHolidayService.findAllYears();
        model.addAttribute("years", years);
        model.addAttribute("selectedYear", year);

        return "admin/manage-ph/view-all";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("publicHoliday", new PublicHoliday());
        return "admin/manage-ph/add-ph-form";  // Make sure this template exists
    }

    @PostMapping("/add")
    public String createPublicHoliday(@ModelAttribute PublicHoliday publicHoliday, Model model) {
        boolean isSaved = publicHolidayService.save(publicHoliday);
        if (isSaved) {
            model.addAttribute("publicHoliday", publicHoliday);
            return "admin/manage-ph/successful-update";  // Make sure this template exists
        }
        model.addAttribute("error", "Error saving public holiday");
        return "admin/manage-ph/add-ph-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<PublicHoliday> publicHoliday = publicHolidayService.findById(id);
        if (publicHoliday.isPresent()) {
            model.addAttribute("publicHoliday", publicHoliday.get());
            return "admin/manage-ph/edit-ph-form";  // Make sure this template exists
        }
        return "redirect:/admin/manage-ph/";
    }

    @PostMapping("/update/{id}")
    public String updatePublicHoliday(@PathVariable Integer id, @ModelAttribute PublicHoliday publicHoliday, Model model) {
        publicHoliday.setId(id);
        if (publicHolidayService.update(publicHoliday)) {
            model.addAttribute("publicHoliday", publicHoliday);
            return "admin/manage-ph/successful-update";  // Make sure this template exists
        }
        return "admin/manage-ph/edit-ph-form";
    }

    @GetMapping("/delete/{id}")
    public String deletePublicHoliday(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = publicHolidayService.deleteById(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Public holiday deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete public holiday.");
        }
        return "redirect:/admin/manage-ph/";
    }
}
